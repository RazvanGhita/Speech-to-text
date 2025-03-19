package com.wtztechnologies.speechtotext.mappers.team;

import com.wtztechnologies.speechtotext.entities.Team;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.team.TeamRequestModel;

import org.modelmapper.PropertyMap;


@CustomMapping
public class TeamPropertyMap extends PropertyMap<TeamRequestModel, Team> {
    @Override
    protected void configure() {

    }
}
