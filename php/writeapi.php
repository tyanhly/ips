<?php
#die('dfdf:');
$file = "/data/" . $_GET['file'] .".txt" ;


#echo $file;die;
#echo "ehhe";die;
unset($_GET['file']);
$str ="";
foreach($_GET as $k => $v){

$str .= $v ;
}
echo $str;
if(filesize($file)<1000*1000*5){
`echo '$str' >> $file`;
}
#file_put_contents($file,$str);
