<?php
require "vocality_connection.php";

$username = $_POST["username"];
$song_title = $_POST["title"];
$fileKey = $_POST["fileKey"];

//declare a prepared statement and bind
$stmt = $conn->prepare("SELECT user_id FROM user_information WHERE username = ?");
$stmt->bind_param("s", $user);

//set parameters and execute
$user = $username;
$stmt->execute();
$result = $stmt->get_result();
$stmt->close();

//get the user's user_id
$user_id;
while($row = $result->fetch_assoc())
{
    $user_id = "$row[user_id]";
}

//insert the user's associated recording
$stmt = $conn->prepare("INSERT INTO user_recordings (id, user_id, title, aws_s3_key) VALUES (?, ?, ?, ?)");
$stmt->bind_param("iiss", $id, $u_id, $title, $key);

//set parameters and execute
$id = NULL;
$u_id = $user_id;
$title = $song_title;
$key = $fileKey;
$stmt->execute();
$stmt->close();

mysqli_close($conn);
?>