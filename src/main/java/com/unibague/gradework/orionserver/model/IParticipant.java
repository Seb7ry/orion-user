package com.unibague.gradework.orionserver.model;

import java.util.List;

/**
 * The IParticipant interface defines the contract for participant entities in the system.
 * This interface can be implemented by classes representing participants in various contexts,
 * such as events, projects, or teams.
 */
public interface IParticipant {

    /**
     * Retrieves a list of users associated with the participant.
     *
     * @return a List of {@link User} objects representing the participant's associated individuals.
     */
    List<User> getParticipantPerson();

    /**
     * Retrieves the role of the participant.
     *
     * @return a String representing the participant's role (e.g., "Leader", "Member").
     */
    String getParticipantRole();
}
