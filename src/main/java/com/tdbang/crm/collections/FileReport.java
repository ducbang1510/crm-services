package com.tdbang.crm.collections;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "file_report")
public class FileReport {
    @Id
    private String id;

    private String filename;
    private byte[] file;

    private Date createdDate;
}