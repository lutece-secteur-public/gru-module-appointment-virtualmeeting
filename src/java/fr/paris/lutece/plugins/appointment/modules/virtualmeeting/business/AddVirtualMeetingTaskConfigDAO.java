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

package fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business;

import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;

import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

/**
 * This class provides Data Access methods for AddVirtualMeetingTaskConfig objects
 */
public class AddVirtualMeetingTaskConfigDAO implements ITaskConfigDAO<AddVirtualMeetingTaskConfig>
{
    // Constants
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_addvirtualmeeting_config ( id_task, provider, id_entry_user_link, id_entry_agent_link ) VALUES ( ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_addvirtualmeeting_config WHERE id_task = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_addvirtualmeeting_config SET provider = ?, id_entry_user_link = ?, id_entry_agent_link = ? WHERE id_task = ?";

    private static final String SQL_QUERY_SELECTALL = "SELECT id_task, provider, id_entry_user_link, id_entry_agent_link FROM workflow_task_addvirtualmeeting_config";
    private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + " WHERE id_task = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( AddVirtualMeetingTaskConfig addVirtualMeetingTaskConfig )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, addVirtualMeetingTaskConfig.getIdTask( ) );
            daoUtil.setString( nIndex++, addVirtualMeetingTaskConfig.getProvider( ) );
            daoUtil.setInt( nIndex++, addVirtualMeetingTaskConfig.getIdEntryUserLink( ) );
            daoUtil.setInt( nIndex++, addVirtualMeetingTaskConfig.getIdEntryAgentLink( ) );

            daoUtil.executeUpdate( );

        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( AddVirtualMeetingTaskConfig addVirtualMeetingTaskConfig )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, addVirtualMeetingTaskConfig.getProvider( ) );
            daoUtil.setInt( nIndex++, addVirtualMeetingTaskConfig.getIdEntryUserLink( ) );
            daoUtil.setInt( nIndex++, addVirtualMeetingTaskConfig.getIdEntryAgentLink( ) );
            daoUtil.setInt( nIndex, addVirtualMeetingTaskConfig.getIdTask( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AddVirtualMeetingTaskConfig load( int nKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            AddVirtualMeetingTaskConfig addVirtualMeetingTaskConfig = null;

            if ( daoUtil.next( ) )
            {
                addVirtualMeetingTaskConfig = loadFromDaoUtil( daoUtil );
            }

            return addVirtualMeetingTaskConfig;
        }
    }

    /**
     * Create a AddVirtualMeetingTaskConfig from daoUtil
     * 
     * @param request
     *            request
     * @param locale
     *            locale
     * @param task
     *            the task
     * @return TaskTestConfig
     */
    private AddVirtualMeetingTaskConfig loadFromDaoUtil( DAOUtil daoUtil )
    {

        AddVirtualMeetingTaskConfig addVirtualMeetingTaskConfig = new AddVirtualMeetingTaskConfig( );
        int nIndex = 1;

        addVirtualMeetingTaskConfig.setIdTask( daoUtil.getInt( nIndex++ ) );
        addVirtualMeetingTaskConfig.setProvider( daoUtil.getString( nIndex++ ) );
        addVirtualMeetingTaskConfig.setIdEntryUserLink( daoUtil.getInt( nIndex++ ) );
        addVirtualMeetingTaskConfig.setIdEntryAgentLink( daoUtil.getInt( nIndex ) );

        return addVirtualMeetingTaskConfig;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }
}
