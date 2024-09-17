// src/components/LatestTradesList.tsx
import React from "react";
import { List, ListItem, ListItemText, Paper, Typography } from "@mui/material";
import { Trade } from "../types";

interface LatestTradesListProps {
  trades: Trade[];
}

const LatestTradesList: React.FC<LatestTradesListProps> = ({ trades }) => {
  return (
    <Paper
      elevation={3}
      sx={{
        padding: 2,
        marginBottom: 2,
        height: "300px",
        overflowY: "hidden",
        overflowX: "hidden",
      }}
    >
      <Typography variant="h6" gutterBottom>
        Latest Trades
      </Typography>
      <List sx={{ maxHeight: "250px", overflow: "auto" }}>
        {trades.map((trade, index) => (
          <ListItem key={index} divider>
            <ListItemText
              primary={`${trade.ticker} - ${trade.name}`}
              secondary={
                `${trade.date} - ${trade.strategyName}` +
                (trade.orderType === "Buy" ? " - Buy" : " - Sell")
              }
            />
          </ListItem>
        ))}
      </List>
    </Paper>
  );
};

export default LatestTradesList;
