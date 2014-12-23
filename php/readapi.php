<?php
#die('dfdf:');

$lineNumber = 1000;
$file = "/data/" . $_GET['file'] . ".txt" ;

#$content = readline($file,10000);
$content = `tail -n $lineNumber $file`;
echo $content;
