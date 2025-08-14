import './NotificationForm.css';
import { observer } from "mobx-react-lite";
import { NotificationService } from "../stores/NotificationService";
import {
    Box,
    Button,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
    TextField,
    Typography,
} from '@mui/material';

interface Props {
    store: NotificationService;
}

export const NotificationForm = observer(({ store }: Props) => {

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await store.submitMessage();
    };

    return (
        <Box
            component="form"
            onSubmit={handleSubmit}
            className="notification-form"
        >
            <Typography variant="h6" className="form-title">
                Send Message
            </Typography>

            <FormControl fullWidth className="form-control">
                <InputLabel>Category</InputLabel>
                <Select
                    value={store.selectedCategory}
                    onChange={(e) => store.setCategory(e.target.value)}
                    label="Category"
                >
                    {store.categories.map((cat) => (
                        <MenuItem key={cat} value={cat}>
                            {cat}
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>

            <TextField
                label="Message"
                value={store.message}
                onChange={(e) => store.setMessage(e.target.value)}
                multiline
                fullWidth
                rows={4}
                className="message-field"
            />

            <Button
                type="submit"
                variant="contained"
                color="primary"
            >
                Send
            </Button>
        </Box>
    );
});