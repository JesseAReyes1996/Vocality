<?php
    require "vocality_connection.php";

    $username = $_POST["username"];
    $friendsname = $_POST["friendsname"];

    //get user ID
    $stmt = $conn->prepare("SELECT user_id FROM user_information WHERE username LIKE ?");
    $stmt->bind_param("s",$user);
    $user = $username;
    $stmt->execute();
    $user_id = $stmt->get_result();
    $stmt->close();

    //get friend ID
    $stmt = $conn->prepare("SELECT user_id FROM user_information WHERE friendsname LIKE ?");
    $stmt->bind_param("s",$friend);
    $friend = $friendsname;
    $stmt->execute();
    $friends_id = $stmt->get_result();
    $stmt->close();

    //check if the requested user is friend already 
    $stmt = $conn->prepare("SELECT * FROM friends WHERE user_id LIKE ? AND friend_id LIKE ?");
    $stmt->bind_param("ii",$user,$friend);
    $user = $user_id;
    $friend = $friends_id;
    $stmt->execute();
    $result = $stmt->get_result();
    $stmt->close();

    //if requested user is not your friend 
    if(mysqli_num_rows($result) == 0){
        //send a friend request
        $stmt = $conn->prepare("INSERT INTO friend_request (user_id,friend_id,request_status) VALUES (?,?,0)");
        $stmt->bind_param("ii",$user,$friend);
        $user = $user_id;
        $friend = $friends_id;
        $stmt->execute();
        $stmt->close();
        echo "sended successfully";
    }
    else{
        echo "already";
    }
    mysqli_close($conn);
?>