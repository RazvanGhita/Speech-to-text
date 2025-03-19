package com.wtztechnologies.speechtotext.mappers.task;

import com.wtztechnologies.speechtotext.entities.Task;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.task.TaskResponseModel;
import org.modelmapper.PropertyMap;

@CustomMapping
public class TaskResponseModelPropertyMap extends PropertyMap<TaskResponseModel, Task> {
    @Override
    protected void configure() {

    }
}
