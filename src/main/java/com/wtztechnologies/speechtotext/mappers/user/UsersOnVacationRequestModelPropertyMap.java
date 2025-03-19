package com.wtztechnologies.speechtotext.mappers.user;

import com.wtztechnologies.speechtotext.entities.UsersOnVacation;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.user.UsersOnVacationRequestModel;
import org.modelmapper.PropertyMap;

@CustomMapping
public class UsersOnVacationRequestModelPropertyMap extends PropertyMap<UsersOnVacationRequestModel, UsersOnVacation> {
    @Override
    protected void configure() {

    }
}
