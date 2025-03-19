package com.wtztechnologies.speechtotext.mappers.team;

import com.wtztechnologies.speechtotext.entities.Team;
import com.wtztechnologies.speechtotext.mapping.CustomMapping;
import com.wtztechnologies.speechtotext.models.team.TeamListChild;
import org.modelmapper.PropertyMap;


@CustomMapping
public class TeamListChildPropertyMap extends PropertyMap<Team, TeamListChild> {
    @Override
    protected void configure() {
        map(source.getId()).setTeamId(null);
    }
}