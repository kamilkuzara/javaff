(calibrate rover0 camera1 objective4 waypoint4)
(navigate rover2 waypoint6 waypoint10)
(sample_soil rover2 rover2store waypoint10)
(navigate rover0 waypoint4 waypoint2)
(drop rover2 rover2store)
(navigate rover0 waypoint2 waypoint4)
(navigate rover2 waypoint10 waypoint6)
(communicate_soil_data rover2 general waypoint10 waypoint6 waypoint9)
(navigate rover2 waypoint6 waypoint5)
(sample_soil rover2 rover2store waypoint5)
(navigate rover2 waypoint5 waypoint6)
(communicate_soil_data rover2 general waypoint5 waypoint6 waypoint9)
(take_image rover0 waypoint4 objective1 camera1 high_res)
(communicate_image_data rover0 general objective1 high_res waypoint4 waypoint9)
(calibrate rover1 camera2 objective4 waypoint6)
(take_image rover1 waypoint6 objective1 camera2 low_res)
(communicate_image_data rover1 general objective1 low_res waypoint6 waypoint9)
(navigate rover0 waypoint4 waypoint0)
(sample_soil rover0 rover0store waypoint0)
(navigate rover0 waypoint0 waypoint4)
(communicate_soil_data rover0 general waypoint0 waypoint4 waypoint9)
(drop rover0 rover0store)
(navigate rover0 waypoint4 waypoint2)
(drop rover2 rover2store)
(navigate rover2 waypoint6 waypoint9)
(sample_soil rover0 rover0store waypoint2)
(navigate rover2 waypoint9 waypoint8)
(navigate rover0 waypoint2 waypoint4)
(communicate_soil_data rover0 general waypoint2 waypoint4 waypoint9)
(sample_soil rover2 rover2store waypoint8)
(communicate_soil_data rover2 general waypoint8 waypoint8 waypoint9)
(navigate rover3 waypoint4 waypoint1)
(sample_rock rover3 rover3store waypoint1)
(navigate rover3 waypoint1 waypoint4)
(communicate_rock_data rover3 general waypoint1 waypoint4 waypoint9)
(drop rover3 rover3store)
(navigate rover3 waypoint4 waypoint2)
(sample_rock rover3 rover3store waypoint2)
(drop rover3 rover3store)
(navigate rover3 waypoint2 waypoint4)
(communicate_rock_data rover3 general waypoint2 waypoint4 waypoint9)
(navigate rover3 waypoint4 waypoint8)
(sample_rock rover3 rover3store waypoint8)
(communicate_rock_data rover3 general waypoint8 waypoint8 waypoint9)
