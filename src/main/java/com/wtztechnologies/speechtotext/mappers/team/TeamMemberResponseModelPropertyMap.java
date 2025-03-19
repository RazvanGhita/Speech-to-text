package com.wtztechnologies.speechtotext.mappers.team;

import com.wtztechnologies.speechtotext.entities.UserDetails;
import com.wtztechnologies.speechtotext.models.team.TeamMemberResponseModel;
import org.modelmapper.PropertyMap;

public class TeamMemberResponseModelPropertyMap extends PropertyMap<UserDetails, TeamMemberResponseModel> {
    @Override
    protected void configure() {

    }
}
