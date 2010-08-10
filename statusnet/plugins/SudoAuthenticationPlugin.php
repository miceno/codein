<?php
/**
 * StatusNet, the distributed open-source microblogging tool
 *
 * Plugin that authenticates an user using a master account
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

class SudoAuthenticationPlugin extends Plugin
{
    /* Nickname of the user having sudo capabilities */
    public $sudoer = 'sudo';

    /* 
     * Password to use to authenticate the sudoer.
     * IMPORTANT: Change this to something difficult to guess! 
     */
    public $password = 'secret';

    /* String to use as separator between the sudoer and the username */
    public $separator = '#';

    /* Set this to true if you want to automatically register users */
    public $register = false;

    /* Set domain suffix for automatically registered users */
    public $emailSuffix = '';

    /* Set this to true to avoid sending a confirmation email */
    public $emailConfirmed = true;

    //---interface implementation---//

    function onStartCheckPassword($nickname, $password, &$authenticatedUser)
    {
        if (strpos($nickname, $this->sudoer . $this->separator) === 0)
        {
            // Get the delegated username
            list($prefix, $username) = explode($this->separator, $nickname);
            // Check that the sudoer has provided the valid password
            if ($this->password === $password) 
            {
                // Fetch the user details for the log-in-as username
                $user = User::staticGet('nickname', $username);
                if (!$user && $this->register) {
                    // Generate random password
                    $randomPasswd = uniqid('', true);
                    $randomPasswd = md5($randomPasswd);
                    $randomPasswd = substr($randomPasswd, 0, 8);

                    // Automatically register the user
                    $user = User::register(array(
                        'nickname'  => $username,
                        'password'  => $randomPasswd,
                        'email'     => $username . $this->emailSuffix,
                        'email_confirmed' => $this->emailConfirmed,
                    ));
                }

                if ($user) {
                    // Log in as the sudoer
                    $authenticatedUser = $user;
                    return false;
                }
            }
        }
    }

    function isReadOnly($args)
    {
        return true;
    }

    function onPluginVersion(&$versions)
    {
        $versions[] = array('name' => 'Sudo Authentication',
                            'version' => STATUSNET_VERSION,
                            'author' => 'Iván -DrSlump- Montes',
                            'homepage' => 'http://pollinimini.net',
                            'rawdescription' =>
                            _m('The Sudo Authentication plugin allows a special user (sudo) to log in as if it was another one.'));
        return true;
    }
}


