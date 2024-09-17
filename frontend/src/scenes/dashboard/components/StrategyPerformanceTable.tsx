// src/components/StrategyPerformanceTable.tsx
import React from "react";
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { StrategyPerformance } from "../types";

interface StrategyPerformanceTableProps {
  performanceData: StrategyPerformance[];
}

const StrategyPerformanceTable: React.FC<StrategyPerformanceTableProps> = ({
  performanceData,
}) => {
  return (
    <Paper
      elevation={3}
      sx={{
        padding: 2,
        height: "300px",
        overflowY: "hidden",
        overflowX: "hidden",
      }}
    >
      <Typography variant="h6" gutterBottom>
        Strategy Performance
      </Typography>
      <TableContainer sx={{ maxHeight: "250px" }}>
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell>Strategy Name</TableCell>
              <TableCell>1 Month</TableCell>
              <TableCell>3 Months</TableCell>
              <TableCell>YTD</TableCell>
              <TableCell>1 Year</TableCell>
              <TableCell>3 Years</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {performanceData.map((data, index) => (
              <TableRow key={index}>
                <TableCell>{data.strategyName}</TableCell>
                <TableCell>{data.month1}%</TableCell>
                <TableCell>{data.month3}%</TableCell>
                <TableCell>{data.ytd}%</TableCell>
                <TableCell>{data.year1}%</TableCell>
                <TableCell>{data.year3}%</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );
};

export default StrategyPerformanceTable;
