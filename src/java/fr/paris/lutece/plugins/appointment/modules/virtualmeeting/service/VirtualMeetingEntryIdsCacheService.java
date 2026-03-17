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
package fr.paris.lutece.plugins.appointment.modules.virtualmeeting.service;

import javax.inject.Named;

import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingEntryIds;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;

/**
 * Cache service that stores the mapping from workflow ID to resolved guest/host entry IDs.
 * This avoids repeated DAO lookups (actions → tasks → configs) for the same workflow.
 */
@Named( "appointment-virtualmeeting.entryIdsCacheService" )
public class VirtualMeetingEntryIdsCacheService extends AbstractCacheableService
{
    private static final String CACHE_NAME = "appointment-virtualmeeting.entryIdsCache";
    private static final String CACHE_KEY_PREFIX = "workflow-";

    /**
     * Constructor — initialises the underlying ehcache region.
     */
    public VirtualMeetingEntryIdsCacheService( )
    {
        initCache( CACHE_NAME );
    }

    @Override
    public String getName( )
    {
        return CACHE_NAME;
    }

    /**
     * Retrieves cached entry IDs for the given workflow.
     *
     * @param nIdWorkflow
     *            the workflow ID
     * @return the cached entry IDs, or {@code null} if not in cache
     */
    public VirtualMeetingEntryIds getEntryIds( int nIdWorkflow )
    {
        return (VirtualMeetingEntryIds) getFromCache( CACHE_KEY_PREFIX + nIdWorkflow );
    }

    /**
     * Stores entry IDs in the cache for the given workflow.
     *
     * @param nIdWorkflow
     *            the workflow ID
     * @param entryIds
     *            the entry IDs to cache
     */
    public void putEntryIds( int nIdWorkflow, VirtualMeetingEntryIds entryIds )
    {
        putInCache( CACHE_KEY_PREFIX + nIdWorkflow, entryIds );
    }
}
