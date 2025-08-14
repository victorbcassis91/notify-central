import { useEffect } from "react";
import { NotificationService } from "./stores/NotificationService";
import {NotificationForm} from "./components/NotificationForm";
import {NotificationLog} from "./components/NotificationLog";
import {Alert, Box, Snackbar} from '@mui/material';
import {observer} from "mobx-react-lite";
import './App.css';

const notificationStore = new NotificationService();

export const App = observer(() => {
    useEffect(() => {
        notificationStore.fetchCategories();
        notificationStore.fetchLogs();
    }, []);

    return (
        <>
            {/* Mensagem no topo */}
            <Snackbar
                open={!!notificationStore.notificationMessage}
                autoHideDuration={3000}
                onClose={() => notificationStore.notificationMessage = null}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
            >
                <Alert
                    onClose={() => notificationStore.notificationMessage = null}
                    severity={notificationStore.notificationType || 'info'}
                    className="alert"
                >
                    {notificationStore.notificationMessage}
                </Alert>
            </Snackbar>

            <Box className="main-container">
                <Box className="log-box">
                    <NotificationLog store={notificationStore} />
                </Box>

                <Box className="form-box">
                    <NotificationForm store={notificationStore} />
                </Box>
            </Box>
        </>
    );
});