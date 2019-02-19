<?php
    require "vocality_connection.php";

    $username = $_POST["username"];

    $stmt = $conn->prepare("SELECT username FROM user_information WHERE user_id in 
                                (SELECT friend_id FROM friends WHERE user_id IN 
                                    (SELECT user_id FROM user_information WHERE username LIKE ?))");
    $stmt->bind_param("s",$user);
    $user = $username;
    $stmt->execute();
    $result = $stmt->get_result();
    $stmt->close();

    if(mysqli_num_rows($result) != 0){
        echo "$result";
    }
    else{
        echo "no friend!";
    }
    mysqli_close();
?>