(navigate rover0 waypoint1 waypoint0)
(sample_rock rover0 rover0store waypoint0)
(navigate rover0 waypoint0 waypoint1)
(communicate_rock_data rover0 general waypoint0 waypoint1 waypoint0)
(navigate rover1 waypoint3 waypoint2)
(sample_soil rover1 rover1store waypoint2)
(communicate_soil_data rover1 general waypoint2 waypoint2 waypoint0)
(navigate rover1 waypoint2 waypoint3)
(navigate rover1 waypoint3 waypoint0)
(navigate rover1 waypoint0 waypoint1)
(calibrate rover1 camera1 objective0 waypoint1)
(take_image rover1 waypoint1 objective0 camera1 colour)
(communicate_image_data rover1 general objective0 colour waypoint1 waypoint0)