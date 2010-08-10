<?php
/**
 * StatusNet, the distributed open-source microblogging tool
 *
 * Injects a Javascript code to convert Subversion generated messages to links
 *
 * PHP version 5
 *
 * LICENCE: This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @category  Plugin
 * @package   StatusNet
 * @author    Iván -DrSlump- Montes <drslump@pollinimini.net>
 * @copyright 2010 Iván -DrSlump- Montes
 * @license   http://www.fsf.org/licensing/licenses/agpl-3.0.html GNU Affero General Public License version 3.0
 * @link      http://status.net/
 */

if (!defined('STATUSNET') && !defined('LACONICA')) {
    exit(1);
}

class SvnLinksPlugin extends Plugin
{
    /* Tag-Revision separator character */
    public $separator = '~';

    /* WebSvn url */
    public $url = 'http://host/wsvn/{REPOSITORY}/?op=revision&rev={REVISION}';

 
    function isReadOnly($args)
    {
        return true;
    }

    function onPluginVersion(&$versions)
    {
        $versions[] = array('name' => 'SvnLinks',
                            'version' => STATUSNET_VERSION,
                            'author' => 'Iván -DrSlump- Montes',
                            'homepage' => 'http://pollinimini.net',
                            'rawdescription' =>
                            _m('Converts revisions in links to WebSvn.'));
        return true;
    }

    function onEndShowScripts($action) {
        $action->inlineScript("
		(function(){
  			var sep = '$this->separator';
  			var url = '$this->url';

			// Find all hash tags
  			$('.tag').each(function(){
    
    			if (this.nextSibling && this.nextSibling.nodeType === 3) {
      				var text = this.nextSibling.nodeValue;
					// Check if the next sibling's text contents start with the separator
      				if (text && text.charAt(0) === sep) {
						// Get the repository name from the hash tag
        				var repo = $(this).text();

        				// Prepare the text to be htmlizied 
        				text = text.replace(/&/g, '&amp;')
                   			       .replace(/</g, '&lt;')
                   				   .replace(/>/g, '&gt;');

                        // Convert the revision to url
        				text = text.replace(/^~([0-9]+)/, function(all, a){
          					var href = url.replace('{REPOSITORY}', repo)
                        	          	  .replace('{REVISION}', a); 
          					return sep + '<a class=\"rev\" href=\"' + href + '\">' + a + '</a>'
        				});

        				// Insert the html in the DOM
        				$(this.nextSibling).after(text).remove();
      				}
    			}
  			});  
		})();");
    }
}


