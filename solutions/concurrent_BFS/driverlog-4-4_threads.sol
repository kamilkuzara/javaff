(board-truck driver3 truck2 s0)
(walk driver2 s1 p0-1)
(walk driver2 p0-1 s0)
(load-truck package3 truck2 s0)
(drive-truck truck2 s0 s1 driver3)
(load-truck package4 truck2 s1)
(drive-truck truck2 s1 s0 driver3)
(unload-truck package4 truck2 s0)
(drive-truck truck2 s0 s2 driver3)
(unload-truck package3 truck2 s2)
(load-truck package1 truck2 s2)
(drive-truck truck2 s2 s1 driver3)
(unload-truck package1 truck2 s1)
(drive-truck truck2 s1 s2 driver3)
(disembark-truck driver3 truck2 s2)
(walk driver3 s2 p1-2)
(walk driver3 p1-2 s1)