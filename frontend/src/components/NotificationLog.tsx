import './NotificationLog.css';
import {observer} from "mobx-react-lite";
import {NotificationService} from "../stores/NotificationService";
import {
    Box,
    Typography,
    List,
    ListItem,
    ListItemText,
    Chip, TablePagination,
} from '@mui/material';

interface Props {
    store: NotificationService;
}

export const NotificationLog = observer(({store}: Props) => {

    return (
        <Box className="notification-history">
            <Typography variant="h6" className="history-title">
                Notification History
            </Typography>

            {store.logs?.totalElements === 0 ? (
                <Typography variant="body1" className="empty-message">
                    No logs found
                </Typography>
            ) : (
                <List dense>
                    {store.logs?.content.map((log, idx) => (
                        <div key={idx}>
                            <ListItem alignItems="flex-start">
                                <ListItemText
                                    primary={
                                        <>
                                            <Typography
                                                component="span"
                                                variant="body2"
                                                className="log-timestamp"
                                            >
                                                {log.timestamp}
                                            </Typography>
                                            {' — '}
                                            <Chip
                                                label={log.category}
                                                size="small"
                                                className="log-chip"
                                            />
                                            {`via ${log.channel}:`}
                                        </>
                                    }
                                    secondary={
                                        <Typography
                                            variant="body2"
                                            className="log-message"
                                        >
                                            {log.message}
                                        </Typography>
                                    }
                                />
                            </ListItem>
                            {/*{idx < store.logs?.totalElements - 1 && <Divider />}*/}
                        </div>
                    ))}
                </List>
            )}

            <TablePagination
                component="div"
                count={store.logs?.totalElements}
                page={store.logs?.page}
                onPageChange={store.handleChangePage}
                rowsPerPage={store.logs?.size}
                onRowsPerPageChange={store.handleChangeRowsPerPage}
                rowsPerPageOptions={[5, 10, 20, 50]}
            />
        </Box>
    );
});