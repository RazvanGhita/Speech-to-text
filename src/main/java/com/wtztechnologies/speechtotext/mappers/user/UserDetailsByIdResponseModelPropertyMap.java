package com.wtztechnologies.speechtotext.mappers.user;

import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.user.UserDetailsByIdResponseModel;
import org.modelmapper.PropertyMap;

@CustomMapping
public class UserDetailsByIdResponseModelPropertyMap extends PropertyMap<UserDetailsByIdResponseModel, UserDetails> {
    @Override
    protected void configure() {

    }
}
