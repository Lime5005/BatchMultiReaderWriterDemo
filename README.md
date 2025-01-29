- Make sure you have MySQL running in localhost, and configure the file `application.properties`.
- Make sure the tables are created: 
```
mysql> show tables;
+-------------------------------+
| Tables_in_batch_reader_writer |
+-------------------------------+
| BATCH_JOB_EXECUTION           |
| BATCH_JOB_EXECUTION_CONTEXT   |
| BATCH_JOB_EXECUTION_PARAMS    |
| BATCH_JOB_EXECUTION_SEQ       |
| BATCH_JOB_INSTANCE            |
| BATCH_JOB_SEQ                 |
| BATCH_STEP_EXECUTION          |
| BATCH_STEP_EXECUTION_CONTEXT  |
| BATCH_STEP_EXECUTION_SEQ      |
+-------------------------------+
```
If not, refer to the doc and create manually:
https://github.com/spring-projects/spring-batch/blob/main/spring-batch-core/src/main/resources/org/springframework/batch/core/schema-mysql.sql

- Launch the project by running the entry point file, then you can see the results in the output/ directory.

- Remember to delete the contents in the output/ directory before restarting.

- Each of the 3 TEXT files in `resources` has 20 lines. The application will read these files, process the data, and then write down 3 CSV files with the same file names but different data after processing.
