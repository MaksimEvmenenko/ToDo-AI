# Reminder Feature - Quick Start Guide

## Quick Overview
The reminder feature allows you to set scheduled notifications for your to-do items. Notifications are automatically sent when the scheduled time arrives.

## Step-by-Step Usage

### Step 1: Create a To-Do Item
First, create a new to-do that you want to set a reminder for.

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/todo \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Team Meeting",
    "description": "Quarterly review with team leads"
  }'
```

**Response (note the `id`):**
```json
{
  "id": 1,
  "title": "Team Meeting",
  "description": "Quarterly review with team leads",
  "completed": false,
  "reminderEnabled": false,
  "createdAt": "2026-04-02T10:00:00",
  "updatedAt": "2026-04-02T10:00:00"
}
```

### Step 2: Create a Reminder for the To-Do
Now create a reminder that will notify you at a specific time.

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/reminder \
  -H "Content-Type: application/json" \
  -d '{
    "todoId": 1,
    "scheduledTime": "2026-04-05T14:00:00",
    "notificationMessage": "Meeting starts in 30 minutes, prepare your notes!"
  }'
```

**Response:**
```json
{
  "id": 1,
  "todoId": 1,
  "scheduledTime": "2026-04-05T14:00:00",
  "sent": false,
  "notificationMessage": "Meeting starts in 30 minutes, prepare your notes!",
  "createdAt": "2026-04-02T10:00:00",
  "updatedAt": "2026-04-02T10:00:00"
}
```

Notice that:
- `sent` is `false` (notification hasn't been sent yet)
- `scheduledTime` is in the future

### Step 3: Verify Reminder Creation
Check that the reminder was created and the to-do now has reminders enabled.

**Get the updated todo:**
```bash
curl http://localhost:8080/api/v1/todo/1
```

**Response:**
```json
{
  "id": 1,
  "title": "Team Meeting",
  "description": "Quarterly review with team leads",
  "completed": false,
  "reminderEnabled": true,
  "createdAt": "2026-04-02T10:00:00",
  "updatedAt": "2026-04-02T10:00:00"
}
```

Notice `reminderEnabled` is now `true`.

### Step 4: Automatic Notification (Wait for Scheduled Time)
The system automatically checks for pending reminders every 60 seconds. When the scheduled time arrives, the notification will be sent automatically.

**Check application logs for:**
```
INFO com.todoai.service.ReminderService - Reminder notification sent for todo: 1
```

### Step 5: Verify Notification Was Sent
After the scheduled time has passed, check the reminder status.

**Request:**
```bash
curl http://localhost:8080/api/v1/reminder/1
```

**Response:**
```json
{
  "id": 1,
  "todoId": 1,
  "scheduledTime": "2026-04-05T14:00:00",
  "sent": true,
  "notificationMessage": "Meeting starts in 30 minutes, prepare your notes!",
  "createdAt": "2026-04-02T10:00:00",
  "updatedAt": "2026-04-02T10:00:00"
}
```

Notice `sent` is now `true`.

## Testing Without Waiting

For testing purposes, you can manually trigger a reminder notification without waiting for the scheduled time.

### Trigger Notification Immediately
```bash
curl -X POST http://localhost:8080/api/v1/reminder/1/send
```

**Response:**
```
Notification sent successfully for reminder: 1
```

This is useful for testing the notification system without having to wait for the scheduled time.

## Common API Operations

### Get All Reminders
```bash
curl http://localhost:8080/api/v1/reminder/list
```

### Get Reminders for a Specific Todo
```bash
curl http://localhost:8080/api/v1/reminder/todo/1
```

### Update a Reminder
Change the scheduled time or message:
```bash
curl -X PUT http://localhost:8080/api/v1/reminder/1 \
  -H "Content-Type: application/json" \
  -d '{
    "scheduledTime": "2026-04-05T15:00:00",
    "notificationMessage": "Updated: Meeting at 3 PM"
  }'
```

### Delete a Reminder
```bash
curl -X DELETE http://localhost:8080/api/v1/reminder/1
```

## Advanced Usage

### Multiple Reminders per To-Do
You can create multiple reminders for the same to-do item with different scheduled times.

```bash
# First reminder - 1 hour before
curl -X POST http://localhost:8080/api/v1/reminder \
  -H "Content-Type: application/json" \
  -d '{
    "todoId": 1,
    "scheduledTime": "2026-04-05T13:00:00",
    "notificationMessage": "Meeting in 1 hour"
  }'

# Second reminder - 30 minutes before
curl -X POST http://localhost:8080/api/v1/reminder \
  -H "Content-Type: application/json" \
  -d '{
    "todoId": 1,
    "scheduledTime": "2026-04-05T13:30:00",
    "notificationMessage": "Meeting in 30 minutes - get ready!"
  }'
```

### Get All Reminders for a Todo
```bash
curl http://localhost:8080/api/v1/reminder/todo/1
```

## How It Works Behind the Scenes

1. **Automatic Checking**: Every 60 seconds, the system checks for reminders whose scheduled time has passed
2. **Notification Sending**: For each pending reminder, the system sends a notification to the configured endpoint
3. **Status Update**: Once sent, the reminder is marked as "sent" in the database
4. **Error Handling**: If a notification fails, it's logged but doesn't prevent other reminders from being processed

## Configuration

The reminder system is configured in `application.properties`:

```properties
# Where to send notifications (local endpoint)
reminder.notification.endpoint=http://localhost:8080/api/v1/notification/send

# How often to check for pending reminders (in milliseconds)
reminder.check.interval=60000
```

You can adjust these settings as needed:
- Change `reminder.check.interval` to check more or less frequently (e.g., `30000` for every 30 seconds)
- Change `reminder.notification.endpoint` to send notifications to an external service

## Important Notes

- **Date/Time Format**: Use ISO 8601 format: `YYYY-MM-DDTHH:MM:SS`
- **Scheduled Time**: Must be a future date/time when creating a reminder
- **Timezone**: Times are in your server's timezone (configure via `spring.jpa.properties.hibernate.jdbc.time_zone`)
- **Database**: Reminders are stored in a `reminders` table in your database
- **Notifications**: Currently logged; extend the system to add email, SMS, or push notifications

## Troubleshooting

### Reminder Not Sending
1. Check that the scheduled time is in the past
2. Verify the reminder `sent` field is `false`
3. Check application logs for errors
4. Ensure the notification endpoint is accessible

### Can't Create Reminder
1. Make sure the `todoId` exists (call GET `/api/v1/todo/{id}` first)
2. Verify the `scheduledTime` is in valid ISO 8601 format
3. Check application logs for error messages

### Testing a Reminder Quickly
Create a reminder with a scheduled time a few seconds in the past, then wait for the next scheduler run (within 60 seconds):

```bash
# Create a reminder scheduled for 2 minutes ago
curl -X POST http://localhost:8080/api/v1/reminder \
  -H "Content-Type: application/json" \
  -d '{
    "todoId": 1,
    "scheduledTime": "2026-04-02T11:58:00",
    "notificationMessage": "This should send immediately"
  }'

# Check logs after 60 seconds (or manually trigger)
curl -X POST http://localhost:8080/api/v1/reminder/1/send
```

## Next Steps

- Set up email notifications by extending the `NotificationController`
- Add SMS notifications using Twilio
- Implement push notifications for mobile apps
- Create a frontend interface for managing reminders
- Set up recurring reminders with cron expressions

