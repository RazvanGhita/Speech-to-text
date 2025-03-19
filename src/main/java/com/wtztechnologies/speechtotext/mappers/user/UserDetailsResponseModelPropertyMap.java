package com.wtztechnologies.speechtotext.mappers.user;

import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.user.UserDetailsResponseModel;

import org.modelmapper.PropertyMap;

@CustomMapping
public class UserDetailsResponseModelPropertyMap extends PropertyMap<UserDetailsResponseModel, UserDetails> {
    @Override
    protected void configure() {

    }
}