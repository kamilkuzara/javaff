(calibrate rover0 camera0 objective0 waypoint0)
(take_image rover0 waypoint0 objective1 camera0 low_res)
(communicate_image_data rover0 general objective1 low_res waypoint0 waypoint1)
(sample_rock rover0 rover0store waypoint0)
(communicate_rock_data rover0 general waypoint0 waypoint0 waypoint1)
(drop rover0 rover0store)
(sample_soil rover0 rover0store waypoint0)
(communicate_soil_data rover0 general waypoint0 waypoint0 waypoint1)
