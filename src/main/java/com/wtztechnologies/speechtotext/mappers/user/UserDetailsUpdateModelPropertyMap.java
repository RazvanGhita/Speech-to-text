package com.wtztechnologies.speechtotext.mappers.user;

import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.user.UserDetailsUpdateModel;

import org.modelmapper.PropertyMap;


@CustomMapping
public class UserDetailsUpdateModelPropertyMap extends PropertyMap<UserDetailsUpdateModel, UserDetails> {
    @Override
    protected void configure() {
       skip(destination.getPassword());
    }
}
