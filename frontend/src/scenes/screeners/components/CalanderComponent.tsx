import React, { useState, useEffect } from "react";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { Button } from "@mui/material";
import dayjs, { Dayjs } from "dayjs";

interface CalendarComponentProps {
  initialDate: string; // Expecting date in 'yyyymmdd' format
  onDateUpdate: (formattedDate: string) => void; // Callback function to pass the formatted date
}

const CalendarComponent: React.FC<CalendarComponentProps> = ({
  initialDate,
  onDateUpdate,
}) => {
  // Convert initialDate (yyyymmdd) to a Dayjs object or set to null
  const [selectedDate, setSelectedDate] = useState<Dayjs | null>(
    initialDate ? dayjs(initialDate, "YYYYMMDD") : null
  );

  // Function to format date to 'yyyymmdd'
  const formatDate = (date: Dayjs | null): string => {
    return date ? date.format("YYYYMMDD") : "";
  };

  // Handle date change and trigger the onDateUpdate callback
  const handleDateChange = (newValue: Dayjs | null) => {
    setSelectedDate(newValue);
  };

  // Notify the parent component when the component first loads if there's an initial date
  useEffect(() => {
    if (initialDate && onDateUpdate) {
      onDateUpdate(initialDate);
    }
  }, [initialDate, onDateUpdate]);

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <DatePicker
        label="Select Date"
        value={selectedDate}
        onChange={handleDateChange}
        format="YYYY/MM/DD"
        maxDate={dayjs(new Date().getTime())}
      />
      <Button onClick={() => onDateUpdate(formatDate(selectedDate))}>Go</Button>
    </LocalizationProvider>
  );
};

export default CalendarComponent;
