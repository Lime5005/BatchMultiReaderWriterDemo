1, Make sure you have MySQL running in localhost, and configure the file `application.properties`.
2, Make sure the tables are created: 
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
If not, refer to the doc and create manually:
https://github.com/spring-projects/spring-batch/blob/main/spring-batch-core/src/main/resources/org/springframework/batch/core/schema-mysql.sql

3, Launch the project by running the entry point file, then you can see the results in the output/ directory.

4, Remember to delete the contents in the output/ directory before restarting.

5, There are 20 lines for each of the 3 TEXT files in the `resources` folder, the application will read these files and then write down 3 CSV files with the same file names.
