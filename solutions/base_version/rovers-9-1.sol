(calibrate rover2 camera3 objective0 waypoint0)
(navigate rover2 waypoint0 waypoint3)
(navigate rover3 waypoint2 waypoint6)
(sample_soil rover3 rover3store waypoint6)
(communicate_soil_data rover3 general waypoint6 waypoint6 waypoint4)
(take_image rover2 waypoint3 objective2 camera3 colour)
(communicate_image_data rover2 general objective2 colour waypoint3 waypoint4)
(calibrate rover3 camera0 objective2 waypoint6)
(take_image rover3 waypoint6 objective2 camera0 low_res)
(communicate_image_data rover3 general objective2 low_res waypoint6 waypoint4)
(navigate rover1 waypoint2 waypoint0)
(sample_rock rover1 rover1store waypoint0)
(drop rover1 rover1store)
(navigate rover1 waypoint0 waypoint2)
(navigate rover1 waypoint2 waypoint1)
(communicate_rock_data rover1 general waypoint0 waypoint1 waypoint4)
(navigate rover1 waypoint1 waypoint3)
(sample_rock rover1 rover1store waypoint3)
(communicate_rock_data rover1 general waypoint3 waypoint3 waypoint4)
(drop rover1 rover1store)
(navigate rover1 waypoint3 waypoint1)
(navigate rover1 waypoint1 waypoint2)
(navigate rover1 waypoint2 waypoint6)
(sample_rock rover1 rover1store waypoint6)
(communicate_rock_data rover1 general waypoint6 waypoint6 waypoint4)
(navigate rover2 waypoint3 waypoint0)
(sample_soil rover2 rover2store waypoint0)
(navigate rover2 waypoint0 waypoint3)
(communicate_soil_data rover2 general waypoint0 waypoint3 waypoint4)
(drop rover2 rover2store)
(navigate rover2 waypoint3 waypoint4)
(sample_soil rover2 rover2store waypoint4)
(navigate rover2 waypoint4 waypoint3)
(communicate_soil_data rover2 general waypoint4 waypoint3 waypoint4)