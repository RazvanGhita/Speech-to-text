package com.wtztechnologies.speechtotext.mappers.task;

import com.wtztechnologies.speechtotext.entities.Task;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.task.TaskUpdateModel;
import org.modelmapper.PropertyMap;

@CustomMapping
public class TaskUpdateModelPropertyMap extends PropertyMap<TaskUpdateModel, Task> {
    @Override
    protected void configure() {
        skip(destination.getUserId());
        skip(destination.getAudioFileName());
        skip(destination.getAudioExtension());
        skip(destination.getDuration());
        skip(destination.getTaskStatus());
    }
}
