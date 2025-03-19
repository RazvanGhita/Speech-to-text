package com.wtztechnologies.speechtotext.mappers.user;

import com.wtztechnologies.speechtotext.entities.UsersOnVacation;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.user.UsersOnVacationMemberRequestModel;
import org.modelmapper.PropertyMap;

@CustomMapping
public class UsersOnVacationMemberRequestModelPropertyMap extends PropertyMap<UsersOnVacationMemberRequestModel, UsersOnVacation> {
    @Override
    protected void configure() {

    }
}
