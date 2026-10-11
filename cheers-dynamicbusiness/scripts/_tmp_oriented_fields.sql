SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness;

SELECT code, name, type, options
FROM dynamic_field
WHERE deleted = false
  AND code IN (
    'pitch','yaw','focal_length','aircraft_heading','payload_position_index',
    'payload_lens_index','use_global_payload_lens','focus_x','focus_y',
    'focus_region_width','focus_region_height','target_angle','accurate_frame_valid',
    'action_duration','gimbal_yaw','gimbal_yaw_rotate_angle','oriented_photo_mode'
  )
ORDER BY code;
