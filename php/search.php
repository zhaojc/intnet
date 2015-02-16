<?php
header('Content-Type: text/html; charset=utf-8');
?>
<html>
	<head>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<script>
		function search(order_field, order) {
			lan = document.getElementById("lan").value;
			typ = document.getElementById("typ").value;
			adress = document.getElementById("adress").value;
			min_area = document.getElementById("min_area").value;
			max_area = document.getElementById("max_area").value;
			min_rum = document.getElementById("min_rum").value;
			max_rum = document.getElementById("max_rum").value;
			min_pris = document.getElementById("min_pris").value;
			max_pris = document.getElementById("max_pris").value;
			min_avgift = document.getElementById("min_avgift").value;
			max_avgift = document.getElementById("max_avgift").value;
			if (lan.length == 0) { 
				document.getElementById("txtHint").innerHTML = "";
				return;
			} else {
				var xmlhttp = new XMLHttpRequest();
				xmlhttp.onreadystatechange = function() {
					if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
						document.getElementById("result").innerHTML = xmlhttp.responseText;
					}
				}
				xmlhttp.open("GET", "result.php?lan=" + lan + "&typ=" + typ + "&adress=" + adress + "&min_area=" + min_area + "&max_area=" + max_area + "&min_rum=" + min_rum + "&max_rum=" + max_rum + "&min_pris=" + min_pris + "&max_pris=" + max_pris + "&min_avgift=" + min_avgift + "&max_avgift=" + max_avgift + "&order_field=" + order_field + "&order=" + order, true);
				xmlhttp.send();
			}
		}
		</script>
	</head>
	
	<body>
		Län: <select id="lan">
		<?php
			$db = new mysqli("mysql-vt2015.csc.kth.se", "cgunningadmin", "Gq3LblFk", "cgunning");
			$db->query("SET NAMES 'utf8'");
			
			$sql = "SELECT DISTINCT(lan) FROM bostader;";
			$stmt = $db->prepare($sql);			
			$stmt->execute();

			$stmt->bind_result($rlan);
			while ($stmt->fetch()) {
				printf ("<option value=\"%s\">%s</option>", $rlan, $rlan);
			}

			$stmt->close();
			$db->close();
		?>
		</select><br />
		Typ: <select id="typ">
		<?php
			$db = new mysqli("mysql-vt2015.csc.kth.se", "cgunningadmin", "Gq3LblFk", "cgunning");
			$db->query("SET NAMES 'utf8'");
			
			$sql = "SELECT DISTINCT(objekttyp) FROM bostader;";
			$stmt = $db->prepare($sql);			
			$stmt->execute();

			$stmt->bind_result($rtyp);
			while ($stmt->fetch()) {
				printf ("<option value=\"%s\">%s</option>", $rtyp, $rtyp);
			}

			$stmt->close();
		?>
		</select><br />
		Adress: <input type="text" id="adress" /><br />
		Min. Area: <input type="text" id="min_area" /><br />
		Max. Area: <input type="text" id="max_area" /><br />
		Min. Rum: <input type="text" id="min_rum" /><br />
		Max. Rum: <input type="text" id="max_rum" /><br />
		Min. Pris: <input type="text" id="min_pris" /><br />
		Max. Pris: <input type="text" id="max_pris" /><br />
		Min. Avgift: <input type="text" id="min_avgift" /><br />
		Max. Avgift: <input type="text" id="max_avgift" /><br />
		<input type="button" onclick="search('pris', 'ASC')" value="Search" />
		<div id="result">
		
		</div>
	</body>

</html>
