package com.example.multireaderwriter.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

@Data
@NoArgsConstructor
public class Customer implements ResourceAware {
    private Long id;
    private String firstName;
    private String lastName;
    private String birthday;
    private String inputSrcFileName;
    private Resource resource;

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
        this.inputSrcFileName = resource.getFilename();
    }
}
