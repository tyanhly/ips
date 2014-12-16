<?php
#die('dfdf:');
$file = "/tmp/" . $_GET['file'] . ".txt";


#echo $file;die;
#echo "ehhe";die;
unset($_GET['file']);
$str ="";
foreach($_GET as $k => $v){

$str .= $v ;
}
echo $str;
`echo '$str' >> $file`;
#file_put_contents($file,$str);
