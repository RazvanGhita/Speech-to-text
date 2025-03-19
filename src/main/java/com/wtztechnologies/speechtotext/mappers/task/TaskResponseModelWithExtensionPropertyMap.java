package com.wtztechnologies.speechtotext.mappers.task;

import com.wtztechnologies.speechtotext.entities.Task;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.task.TaskResponseModelWithExtension;
import org.modelmapper.PropertyMap;

@CustomMapping
public class TaskResponseModelWithExtensionPropertyMap extends PropertyMap<TaskResponseModelWithExtension,Task> {
    @Override
    protected void configure() {

    }
}
