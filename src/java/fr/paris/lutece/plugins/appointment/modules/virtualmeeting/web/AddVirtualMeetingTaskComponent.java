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
package fr.paris.lutece.plugins.appointment.modules.virtualmeeting.web;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.business.form.Form;
import fr.paris.lutece.plugins.appointment.business.form.FormHome;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.AddVirtualMeetingTaskConfig;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingTaskInformation;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.business.VirtualMeetingTaskInformationHome;
import fr.paris.lutece.plugins.appointment.modules.virtualmeeting.provider.IVirtualMeetingProvider;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.workflow.web.task.AbstractTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Web component for the "add virtual meeting" workflow task. Handles configuration form (provider selection) and task history information display.
 */
public class AddVirtualMeetingTaskComponent extends AbstractTaskComponent
{
    // Templates
    private static final String TEMPLATE_TASK_CONFIG = "admin/plugins/workflow/modules/virtualmeeting/task_addvirtualmeeting_config.html";
    private static final String TEMPLATE_TASK_INFORMATION = "admin/plugins/workflow/modules/virtualmeeting/task_addvirtualmeeting_information.html";

    // Markers
    private static final String MARK_CONFIG = "config";
    private static final String MARK_PROVIDER_LIST = "provider_list";
    private static final String MARK_ACCESS_LEVEL_MAP = "access_level_map";
    private static final String MARK_ACCESS_LEVEL_DEFAULT = "access_level_default";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_HOST_URL = "host_url";
    private static final String MARK_ERROR_MESSAGE = "error_message";

    // Beans
    private static final String BEAN_ACTION_SERVICE = "workflow.actionService";

    // I18n
    private static final String PROPERTY_ACCESS_LEVEL_PREFIX = "module.appointment.virtualmeeting.task.config.accessLevel.";

    // Constants
    private static final String RESOURCE_TYPE_APPOINTMENT = "APPOINTMENT_FORM";
    private static final String ENTRY_TYPE_TEXT = "appointment.entryTypeText";
    private static final String ENTRY_TYPE_TEXT_AREA = "appointment.entryTypeTextArea";

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        AddVirtualMeetingTaskConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        if ( config == null )
        {
            config = new AddVirtualMeetingTaskConfig( );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_PROVIDER_LIST, getAvailableProviders( ) );
        model.put( MARK_ACCESS_LEVEL_MAP, getAccessLevelsByProvider( locale ) );
        model.put( MARK_ACCESS_LEVEL_DEFAULT, getDefaultAccessLevelSummary( ) );
        model.put( MARK_ENTRY_LIST, getFormEntries( task ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CONFIG, locale, model );
        return template.getHtml( );
    }

    @Override
    public String validateConfig( ITaskConfig config, HttpServletRequest request )
    {
        return super.validateConfig( config, request );
    }

    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        VirtualMeetingTaskInformation information = VirtualMeetingTaskInformationHome.find( nIdHistory, task.getId( ) );

        if ( information != null )
        {
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_HOST_URL, information.getHostUrl( ) );
            model.put( MARK_ERROR_MESSAGE, information.getErrorMessage( ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_INFORMATION, locale, model );
            return template.getHtml( );
        }

        return null;
    }

    /**
     * Build a ReferenceList of all registered IVirtualMeetingProvider implementations, keyed by {@link IVirtualMeetingProvider#getName()}.
     */
    private ReferenceList getAvailableProviders( )
    {
        ReferenceList list = new ReferenceList( );
        List<IVirtualMeetingProvider> providers = SpringContextService.getBeansOfType( IVirtualMeetingProvider.class );

        for ( IVirtualMeetingProvider provider : providers )
        {
            list.addItem( provider.getName( ), provider.getName( ) );
        }

        return list;
    }

    /**
     * Build, for each registered provider that supports access levels, the ReferenceList of levels it accepts. Providers returning an empty list are omitted so
     * that the template renders no group for them.
     *
     * @param locale
     *            the locale used to resolve the level labels
     * @return a map keyed by provider name, in provider discovery order
     */
    private Map<String, ReferenceList> getAccessLevelsByProvider( Locale locale )
    {
        Map<String, ReferenceList> mapLevels = new LinkedHashMap<>( );

        for ( IVirtualMeetingProvider provider : SpringContextService.getBeansOfType( IVirtualMeetingProvider.class ) )
        {
            ReferenceList levels = new ReferenceList( );

            for ( String strLevel : provider.getSupportedAccessLevels( ) )
            {
                levels.addItem( strLevel, getAccessLevelLabel( strLevel, locale ) );
            }

            if ( !levels.isEmpty( ) )
            {
                mapLevels.put( provider.getName( ), levels );
            }
        }

        return mapLevels;
    }

    /**
     * Summarize what "provider default" resolves to, for display next to the blank option. With a single provider the bare code is returned (e.g.
     * {@code trusted}); with several, each one is listed as {@code name : code}. Providers that cannot tell their default are skipped.
     *
     * @return the summary, or an empty string when no provider reports a default
     */
    private String getDefaultAccessLevelSummary( )
    {
        Map<String, String> mapDefaults = new LinkedHashMap<>( );

        for ( IVirtualMeetingProvider provider : SpringContextService.getBeansOfType( IVirtualMeetingProvider.class ) )
        {
            String strDefault = provider.getDefaultAccessLevel( );

            if ( StringUtils.isNotEmpty( strDefault ) )
            {
                mapDefaults.put( provider.getName( ), strDefault );
            }
        }

        if ( mapDefaults.size( ) == 1 )
        {
            return mapDefaults.values( ).iterator( ).next( );
        }

        return mapDefaults.entrySet( ).stream( ).map( e -> e.getKey( ) + " : " + e.getValue( ) ).collect( Collectors.joining( ", " ) );
    }

    /**
     * Resolve the localized label of an access level, falling back to the raw code when no message is defined — third-party providers may expose levels this
     * module does not know about.
     */
    private String getAccessLevelLabel( String strLevel, Locale locale )
    {
        String strKey = PROPERTY_ACCESS_LEVEL_PREFIX + strLevel;
        String strLabel = I18nService.getLocalizedString( strKey, locale );

        return StringUtils.isEmpty( strLabel ) || strKey.equals( strLabel ) ? strLevel : strLabel;
    }

    /**
     * Build a ReferenceList of form entries from the appointment form linked to the task's workflow. The first item is a blank option (code "0") for "not
     * configured".
     */
    private ReferenceList getFormEntries( ITask task )
    {
        ReferenceList list = new ReferenceList( );
        list.addItem( "0", "" );

        try
        {
            IActionService actionService = SpringContextService.getBean( BEAN_ACTION_SERVICE );
            Action action = actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( action == null || action.getWorkflow( ) == null )
            {
                return list;
            }

            int nIdWorkflow = action.getWorkflow( ).getId( );

            for ( Form form : FormHome.findAllForms( ) )
            {
                if ( form.getIdWorkflow( ) == nIdWorkflow )
                {
                    EntryFilter filter = new EntryFilter( );
                    filter.setIdResource( form.getIdForm( ) );
                    filter.setResourceType( RESOURCE_TYPE_APPOINTMENT );

                    for ( Entry entry : EntryHome.getEntryList( filter ) )
                    {
                        String strBeanName = entry.getEntryType( ).getBeanName( );

                        if ( ENTRY_TYPE_TEXT.equals( strBeanName ) || ENTRY_TYPE_TEXT_AREA.equals( strBeanName ) )
                        {
                            list.addItem( String.valueOf( entry.getIdEntry( ) ), entry.getTitle( ) );
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            AppLogService.error( "AddVirtualMeetingTaskComponent — failed to resolve form entries", e );
        }

        return list;
    }
}
