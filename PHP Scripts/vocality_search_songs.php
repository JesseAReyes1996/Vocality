<?php
require "vocality_connection.php";

$song = $_POST["song"];
$song = "%{$song}%";

//declare a prepared statement and bind
$stmt = $conn->prepare("SELECT * FROM songs WHERE title LIKE ?");
$stmt->bind_param("s", $title);

//set parameters and execute
$title = $song;
$stmt->execute();
$result = $stmt->get_result();
$stmt->close();

if ($result->num_rows > 0)
{
    // output data of each row
    while($row = $result->fetch_assoc())
    {
        echo "$row[artist]<>$row[title]<>$row[aws_s3_key]><";
    }
}
else
{
    echo "no results";
}

mysqli_close($conn);
?>