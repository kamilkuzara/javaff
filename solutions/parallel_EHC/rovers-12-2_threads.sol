(navigate rover2 waypoint7 waypoint0)
(calibrate rover2 camera1 objective1 waypoint0)
(navigate rover3 waypoint7 waypoint0)
(calibrate rover3 camera3 objective3 waypoint0)
(navigate rover1 waypoint4 waypoint6)
(sample_rock rover1 rover1store waypoint6)
(navigate rover1 waypoint6 waypoint4)
(communicate_rock_data rover1 general waypoint6 waypoint4 waypoint2)
(sample_soil rover2 rover2store waypoint0)
(communicate_soil_data rover2 general waypoint0 waypoint0 waypoint2)
(take_image rover2 waypoint0 objective1 camera1 high_res)
(communicate_image_data rover2 general objective1 high_res waypoint0 waypoint2)
(navigate rover3 waypoint0 waypoint7)
(navigate rover3 waypoint7 waypoint3)
(sample_rock rover3 rover3store waypoint3)
(communicate_rock_data rover3 general waypoint3 waypoint3 waypoint2)
(take_image rover3 waypoint3 objective2 camera3 low_res)
(communicate_image_data rover3 general objective2 low_res waypoint3 waypoint2)
(calibrate rover3 camera3 objective3 waypoint3)
(take_image rover3 waypoint3 objective3 camera3 low_res)
(communicate_image_data rover3 general objective3 low_res waypoint3 waypoint2)
