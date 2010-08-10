<?php

define('TWEET_MAX_LENGTH', 140);
define('TWEET_REV', '~');
define('TWEET_ELLIPSIS', '—');
define('TWEET_URL', 'http://localhost/statusnet/api/statuses/update.xml');
define('TWEET_SOURCE', 'svn');
define('TWEET_SUDO', 'sudo');
define('TWEET_PASSWD', 'secret');


$repo = $argv[1]; 
$rev = $argv[2];
$author = $argv[3];
$date = $argv[4];
$msg = $argv[5];

// Format the tweet
$parts = explode('/', $repo);
$repo = array_pop($parts);
$tweet = "#$repo" . TWEET_REV . "$rev $msg";
if (mb_strlen($tweet, 'UTF-8') > TWEET_MAX_LENGTH) {
    $tweet = mb_substr($tweet, 0, TWEET_MAX_LENGTH-1, 'UTF-8') . TWEET_ELLIPSIS;
}


// Send it

$auth = TWEET_SUDO . '#' . $author . ':' . TWEET_PASSWD;
$params = http_build_query(array(
    'source' => TWEET_SOURCE,
    'status' => $tweet,
));

$curl = curl_init();
curl_setopt($curl, CURLOPT_URL, TWEET_URL);
curl_setopt($curl, CURLOPT_CONNECTTIMEOUT, 2);
curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($curl, CURLOPT_POST, 1);
curl_setopt($curl, CURLOPT_POSTFIELDS, $params);
curl_setopt($curl, CURLOPT_USERPWD, $auth);

$result = curl_exec($curl);
$info = curl_getinfo($curl);

curl_close($curl);

if ($info['http_code'] != 200) {
    echo "Error sending message: ${info['http_code']}\n";
    echo $result;
    exit(1);
}


//echo $tweet . PHP_EOL;


