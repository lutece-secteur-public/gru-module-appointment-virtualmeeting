/*
 * Copyright (c) 2002-2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.appointment.modules.virtualmeeting.provider;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.exception.VirtualMeetingException;

/**
 * Technology-agnostic interface for virtual meeting operations. Implement this interface to integrate a specific video-conferencing backend (LiveKit, Jitsi,
 * etc.).
 *
 * <p>
 * Follows the same provider pattern as {@code IFileStoreServiceProvider} in lutece-core: each implementation declares a unique {@link #getName() name} and an
 * optional {@link #isDefault() default} flag used for automatic selection.
 * </p>
 *
 * <p>
 * All provider operations receive their parameters via a {@code Map<String, Object>}. Standard parameter keys are defined as constants on this interface.
 * Providers read only the keys they support and ignore the rest. This allows new provider-specific parameters to be added without changing the interface
 * signature.
 * </p>
 */
public interface IVirtualMeetingProvider
{
    // Standard parameter keys

    /** Room name (String, required for all operations). */
    String PARAM_ROOM_NAME = "roomName";

    /** Seconds to keep the room alive after the last participant leaves (Integer, optional — 0 or absent = server default). Used by {@link #createRoom}. */
    String PARAM_EMPTY_TIMEOUT = "emptyTimeout";

    /** Maximum number of participants (Integer, optional — 0 or absent = unlimited). Used by {@link #createRoom}. */
    String PARAM_MAX_PARTICIPANTS = "maxParticipants";

    /** Participant unique identity, e.g. email or access code (String, required for token/URL generation). */
    String PARAM_IDENTITY = "identity";

    /** Participant display name (String, required for token/URL generation). */
    String PARAM_DISPLAY_NAME = "displayName";

    /** Earliest date/time the token becomes valid ({@link java.util.Date}, optional — {@code null} or absent = immediate validity). */
    String PARAM_NOT_BEFORE = "notBefore";

    /**
     * Room access level (String, optional — absent or empty = provider default). One of the values returned by {@link #getSupportedAccessLevels()}. Used by
     * {@link #createRoom}.
     */
    String PARAM_ACCESS_LEVEL = "accessLevel";

    /**
     * Get the unique name of this provider (e.g. "livekit", "jitsi").
     *
     * @return the provider name
     */
    String getName( );

    /**
     * Whether this provider should be used when no explicit name is configured.
     *
     * @return {@code true} if this is the default provider
     */
    boolean isDefault( );

    /**
     * Get the access levels this provider accepts for {@link #PARAM_ACCESS_LEVEL}, in display order.
     *
     * <p>
     * Returned values are the raw codes sent to the backend (e.g. {@code "public"}, {@code "trusted"}, {@code "restricted"}). Providers whose backend has no
     * such notion return an empty list — the default implementation — and callers then omit the parameter.
     * </p>
     *
     * @return the supported access level codes, never {@code null}
     */
    default List<String> getSupportedAccessLevels( )
    {
        return Collections.emptyList( );
    }

    /**
     * Get the access level this provider will effectively apply when {@link #PARAM_ACCESS_LEVEL} is absent from a {@link #createRoom} call — i.e. its
     * configured site-wide default, or the backend default when nothing is configured.
     *
     * <p>
     * Purely informative: the back-office uses it to tell administrators what "provider default" resolves to. Providers that cannot know the effective value
     * return {@code null} — the default implementation.
     * </p>
     *
     * @return one of {@link #getSupportedAccessLevels()}, or {@code null} if unknown
     */
    default String getDefaultAccessLevel( )
    {
        return null;
    }

    /**
     * Create a new meeting room.
     *
     * <p>
     * Required keys: {@link #PARAM_ROOM_NAME}. Optional keys: {@link #PARAM_EMPTY_TIMEOUT}, {@link #PARAM_MAX_PARTICIPANTS},
     * {@link #PARAM_ACCESS_LEVEL}.
     * </p>
     *
     * @param mapParameters
     *            the operation parameters
     * @return {@code true} if the room was created successfully
     * @throws VirtualMeetingException
     *             if the provider fails to create the room
     */
    boolean createRoom( Map<String, Object> mapParameters ) throws VirtualMeetingException;

    /**
     * Delete an existing room.
     *
     * <p>
     * Required keys: {@link #PARAM_ROOM_NAME}.
     * </p>
     *
     * @param mapParameters
     *            the operation parameters
     * @return {@code true} if the room was deleted successfully
     * @throws VirtualMeetingException
     *             if the provider fails to delete the room
     */
    boolean deleteRoom( Map<String, Object> mapParameters ) throws VirtualMeetingException;

    /**
     * Generate a participant token with full publish and subscribe permissions.
     *
     * <p>
     * Required keys: {@link #PARAM_ROOM_NAME}, {@link #PARAM_IDENTITY}, {@link #PARAM_DISPLAY_NAME}. Optional keys: {@link #PARAM_NOT_BEFORE}.
     * </p>
     *
     * @param mapParameters
     *            the operation parameters
     * @return the authentication token string or URL
     * @throws VirtualMeetingException
     *             if the provider fails to generate the token
     */
    String generateParticipantToken( Map<String, Object> mapParameters ) throws VirtualMeetingException;

    /**
     * Generate a viewer token with subscribe-only permissions (no publish).
     *
     * <p>
     * Required keys: {@link #PARAM_ROOM_NAME}, {@link #PARAM_IDENTITY}, {@link #PARAM_DISPLAY_NAME}. Optional keys: {@link #PARAM_NOT_BEFORE}.
     * </p>
     *
     * @param mapParameters
     *            the operation parameters
     * @return the authentication token string or URL
     * @throws VirtualMeetingException
     *             if the provider fails to generate the token
     */
    String generateViewerToken( Map<String, Object> mapParameters ) throws VirtualMeetingException;

    /**
     * Get the full meeting join URL for a participant.
     * <p>
     * Providers that generate opaque tokens (e.g. JWT) should override this to embed the token into a front-end URL. Providers that already return a complete
     * URL from {@link #generateParticipantToken} can rely on the default implementation.
     * </p>
     *
     * <p>
     * Required keys: {@link #PARAM_ROOM_NAME}, {@link #PARAM_IDENTITY}, {@link #PARAM_DISPLAY_NAME}. Optional keys: {@link #PARAM_NOT_BEFORE}.
     * </p>
     *
     * @param mapParameters
     *            the operation parameters
     * @return the full meeting URL ready for the participant to join
     * @throws VirtualMeetingException
     *             if the provider fails to generate the URL or token
     */
    default String getParticipantMeetingUrl( Map<String, Object> mapParameters ) throws VirtualMeetingException
    {
        return generateParticipantToken( mapParameters );
    }

    /**
     * Get the full meeting join URL for a viewer.
     * <p>
     * Providers that generate opaque tokens (e.g. JWT) should override this to embed the token into a front-end URL. Providers that already return a complete
     * URL from {@link #generateViewerToken} can rely on the default implementation.
     * </p>
     *
     * <p>
     * Required keys: {@link #PARAM_ROOM_NAME}, {@link #PARAM_IDENTITY}, {@link #PARAM_DISPLAY_NAME}. Optional keys: {@link #PARAM_NOT_BEFORE}.
     * </p>
     *
     * @param mapParameters
     *            the operation parameters
     * @return the full meeting URL ready for the viewer to join
     * @throws VirtualMeetingException
     *             if the provider fails to generate the URL or token
     */
    default String getViewerMeetingUrl( Map<String, Object> mapParameters ) throws VirtualMeetingException
    {
        return generateViewerToken( mapParameters );
    }
}
