(calibrate rover1 camera1 objective3 waypoint0)
(calibrate rover1 camera2 objective1 waypoint0)
(navigate rover0 waypoint4 waypoint3)
(take_image rover1 waypoint0 objective2 camera1 colour)
(communicate_image_data rover1 general objective2 colour waypoint0 waypoint1)
(navigate rover1 waypoint0 waypoint6)
(sample_soil rover1 rover1store waypoint6)
(navigate rover1 waypoint6 waypoint0)
(communicate_soil_data rover1 general waypoint6 waypoint0 waypoint1)
(drop rover1 rover1store)
(sample_soil rover1 rover1store waypoint0)
(communicate_soil_data rover1 general waypoint0 waypoint0 waypoint1)
(sample_rock rover2 rover2store waypoint3)
(drop rover2 rover2store)
(navigate rover0 waypoint3 waypoint4)
(navigate rover0 waypoint4 waypoint0)
(navigate rover2 waypoint3 waypoint4)
(communicate_rock_data rover2 general waypoint3 waypoint4 waypoint1)
(navigate rover0 waypoint0 waypoint4)
(navigate rover3 waypoint1 waypoint0)
(sample_rock rover2 rover2store waypoint4)
(communicate_rock_data rover2 general waypoint4 waypoint4 waypoint1)
(drop rover2 rover2store)
(sample_rock rover3 rover3store waypoint0)
(communicate_rock_data rover3 general waypoint0 waypoint0 waypoint1)
(navigate rover0 waypoint4 waypoint3)
(drop rover3 rover3store)
(navigate rover0 waypoint3 waypoint4)
(navigate rover2 waypoint4 waypoint1)
(sample_rock rover2 rover2store waypoint1)
(navigate rover2 waypoint1 waypoint4)
(communicate_rock_data rover2 general waypoint1 waypoint4 waypoint1)
(navigate rover0 waypoint4 waypoint3)
(navigate rover3 waypoint0 waypoint4)
(sample_soil rover0 rover0store waypoint3)
(navigate rover0 waypoint3 waypoint4)
(communicate_soil_data rover0 general waypoint3 waypoint4 waypoint1)
(sample_soil rover3 rover3store waypoint4)
(communicate_soil_data rover3 general waypoint4 waypoint4 waypoint1)
(take_image rover1 waypoint0 objective3 camera2 colour)
(communicate_image_data rover1 general objective3 colour waypoint0 waypoint1)
(calibrate rover1 camera2 objective1 waypoint0)
(take_image rover1 waypoint0 objective3 camera2 low_res)
(communicate_image_data rover1 general objective3 low_res waypoint0 waypoint1)
