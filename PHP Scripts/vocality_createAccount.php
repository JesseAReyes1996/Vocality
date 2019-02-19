<?php
require "vocality_connection.php";

$username = $_POST["username"];
$password = $_POST["password"];

//check if the username is already taken
//declare a prepared statement and bind
$stmt = $conn->prepare("SELECT user_id FROM user_information WHERE username LIKE ?");
$stmt->bind_param("s", $username);
//execute the query and store in result
$stmt->execute();
$result = $stmt->get_result();
$stmt->close();

//if the username is not already taken, create the user's account
if(mysqli_num_rows($result) == 0){
    //declare a prepared statement and bind
    $stmt = $conn->prepare("INSERT INTO user_information (user_id, username, password) VALUES (?, ?, ?)");
    $stmt->bind_param("iss", $user_id, $user, $pass);
    
    //set parameters and execute
    $user_id = NULL;
    $user = $username;
    $pass = $password;
    $stmt->execute();
    $stmt->close();
    echo "created";
}
//the username is already taken, tell the user
else{
    echo "taken";
}

mysqli_close($conn);
?>