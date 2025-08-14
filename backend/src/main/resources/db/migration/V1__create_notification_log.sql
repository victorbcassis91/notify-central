CREATE TABLE notification_log (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  category VARCHAR(40) NOT NULL,
                                  channel VARCHAR(40) NOT NULL,
                                  user_id VARCHAR(50) NOT NULL,
                                  user_name VARCHAR(255) NOT NULL,
                                  message TEXT NOT NULL,
                                  delivered BOOLEAN NOT NULL,
                                  details TEXT,
                                  timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_category ON notification_log(category);
CREATE INDEX idx_timestamp ON notification_log(timestamp);