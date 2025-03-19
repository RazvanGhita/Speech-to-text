package com.wtztechnologies.speechtotext.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
abstract class Audit {

    @Type(type = "org.hibernate.type.LocalDateTimeType")
    @Column(updatable = false)
    @CreatedDate
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    protected LocalDateTime updatedAt;


    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
