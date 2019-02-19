<?php
require "vocality_connection.php";

$username = $_POST["username"];
$password = $_POST["password"];

//declare a prepared statement and bind
$stmt = $conn->prepare("SELECT * FROM user_information WHERE username LIKE ? AND password LIKE ?");
$stmt->bind_param("ss", $user, $pass);

//set parameters and execute
$user = $username;
$pass = $password;
$stmt->execute();
$result = $stmt->get_result();
$stmt->close();

//tell the user they've successfully logged in
if(mysqli_num_rows($result) > 0){
    echo "success";
}
//tell the user their login credentials are incorrect
else{
    echo "fail";
}

mysqli_close($conn);
?>