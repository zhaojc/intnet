<?php
header('Content-Type: text/html; charset=utf-8');
header('Set-Cookie: search=' . $_SERVER['QUERY_STRING']);
?>
<table border="1px solid">
<?php
	$db = new mysqli("mysql-vt2015.csc.kth.se", "cgunningadmin", "Gq3LblFk", "cgunning");
	$db->query("SET NAMES 'utf8'");
	

	$lan = $_GET['lan'];
	$typ = $_GET['typ'];
	$adress = "%" . $_GET['adress'] . "%";
	$min_area = $_GET['min_area'];
	$max_area = $_GET['max_area'];
	$min_rum = $_GET['min_rum'];
	$max_rum = $_GET['max_rum'];
	$min_pris = $_GET['min_pris'];
	$max_pris = $_GET['max_pris'];
	$min_avgift = $_GET['min_avgift'];
	$max_avgift = $_GET['max_avgift'];
	$order_field = $_GET['order_field'];
	$order = $_GET['order'];
	
	$skip_adress = 0;
	$skip_min_area = 0;
	$skip_max_area = 0;
	$skip_min_rum = 0;
	$skip_max_rum = 0;
	$skip_min_pris = 0;
	$skip_max_pris = 0;
	$skip_min_avgift = 0;
	$skip_max_avgift = 0;
	
	if($adress == "")
		$skip_adress = 1;
	if($min_area == "")
		$skip_min_area = 1;
	if($max_area == "")
		$skip_max_area = 1;
	if($min_rum == "")
		$skip_min_rum = 1;
	if($max_rum == "")
		$skip_max_rum = 1;
	if($min_pris == "")
		$skip_min_pris = 1;
	if($max_pris == "")
		$skip_max_pris = 1;
	if($min_avgift == "")
		$skip_min_avgift = 1;
	if($max_avgift == "")
		$skip_max_avgift = 1;

	$sql = "SELECT * FROM bostader WHERE lan = ? AND objekttyp = ? AND (adress LIKE ? OR 1 = ?) AND (area >= ? OR 1 = ?) AND (area <= ? OR 1 = ?) AND (rum >= ? OR 1 = ?) AND (rum <= ? OR 1 = ?) AND (pris >= ? OR 1 = ?) AND (pris <= ? OR 1 = ?) AND (avgift >= ? OR 1 = ?) AND (avgift <= ? OR 1 = ?) ORDER BY ? " + $order + ";";
	if($stmt = $db->prepare($sql)) {
		echo "true";
	} else {
		echo "false";
	}
	printf ("%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />", $lan, $typ, $adress, $min_area, $max_area, $min_rum, $max_rum, $min_pris, $max_pris, $min_avgift, $max_avgift, $order_field, $order);
	
	$stmt->bind_param("sssididiiiiididididis", $lan, $typ, $adress, $min_area, $skip_min_area, $max_area, $skip_max_area, $min_rum, $skip_min_rum, $max_rum, $skip_max_rum, $min_pris, $skip_min_pris, $max_pris, $skip_max_pris, $min_avgift, $skip_min_avgift, $max_avgift, $skip_max_avgift, $order_field);
	printf ("%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />%s<br />", $lan, $typ, $adress, $min_area, $max_area, $min_rum, $max_rum, $min_pris, $max_pris, $min_avgift, $max_avgift);
	$stmt->execute();
	//$stmt->debugDumpParams();

	$stmt->bind_result($rlan, $rtyp, $radress, $rarea, $rrum, $rpris, $ravgift);
	echo "<tr><td onclick=\"search('lan')\">Län</td><td>Typ</td><td>Adress</td><td>Area</td><td>Rum</td><td>Pris</td><td>Avgift</td></tr>";
	while ($stmt->fetch()) {
		echo "<tr>";
		printf ("<td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td>", $rlan, $rtyp, $radress, $rarea, $rrum, $rpris, $ravgift);
		echo "</tr>";
	}
	echo $sql;
	$stmt->close();

?>
</table>
</html>
