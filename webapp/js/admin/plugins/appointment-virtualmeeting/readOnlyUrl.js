( function( ) {
    "use strict";

    var URL_PATTERN = /^(https?:\/\/\S+)$/i;

    function linkifyReadOnlyFields( ) {
        document.querySelectorAll( ".readOnlyUrl" ).forEach( function( el ) {
            var text = el.textContent.trim( );
            if ( URL_PATTERN.test( text ) ) {
                var safeUrl = text.replace( /"/g, "&quot;" ).replace( /'/g, "&#39;" );
                var safeDisplay = text.replace( /</g, "&lt;" ).replace( />/g, "&gt;" );
                el.innerHTML = '<a href="' + safeUrl + '" target="_blank" rel="noopener noreferrer">' + safeDisplay + "</a>";
            }
        } );
    }

    if ( document.readyState === "loading" ) {
        document.addEventListener( "DOMContentLoaded", linkifyReadOnlyFields );
    } else {
        linkifyReadOnlyFields( );
    }
} )( );
