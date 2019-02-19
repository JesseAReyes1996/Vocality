<?php
    require "vocality_connection.php";

    $friendsname = $_POST["username"];

    $stmt = $conn->prepare("SELECT username FROM user_information WHERE username LIKE ?");
    $stmt->bind_param("s",$user);
    $user = $friendsname;
    $stmt->execute();
    $result = $stmt->get_result();
    $stmt->close();

    if(mysqli_num_rows($result) != 0){
        $row=mysqli_fetch_assoc($result);
        printf ("%s\n",$row["username"]);
    }
    else{
        echo "No user found!";
    }
    mysqli_close($conn);
?>