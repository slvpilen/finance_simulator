import React, { useMemo, useState } from "react";
import { useTable, useSortBy, Column } from "react-table";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  TableSortLabel,
  Typography,
} from "@mui/material";
import { styled } from "@mui/system";
import CandlestickChart from "../../../components/chart/CandlestickChart";
import useFetchCandlestickChartData from "../../../hooks/useFetchCandlestickChartData";
import "./RSIScreener.css";
import LoadingAnimation from "../../../components/LoadingAnimation";
import CalendarComponent from "./CalanderComponent";
import { Box } from "@mui/material";

const StickyTableCell = styled(TableCell)({
  backgroundColor: "#b4bfb7",
  color: "#FFFFFF",
  position: "sticky",
  top: 0,
  zIndex: 1,
});

const HoverTableRow = styled(TableRow)(({ selected }) => ({
  cursor: "pointer",
  backgroundColor: selected ? "#d0d0d0" : "inherit",
  "&:hover": {
    backgroundColor: selected ? "#d0d0d0" : "#f0f0f0",
  },
}));

interface ScreenerProps {
  useFetchData: (
    symbolList: string,
    formattedDate: string
  ) => {
    data: any[];
    loading: boolean;
    error: string | null;
  };
  columnsConfig: Column<any>[];
  symbolListName: string;
  subtitle: string;
}

const RSIScreener: React.FC<ScreenerProps> = ({
  useFetchData,
  columnsConfig,
  symbolListName,
}) => {
  const [formattedDate, setFormattedDate] = useState(formatDate("latest"));

  function formatDate(date: string): string {
    return date === "latest"
      ? new Date().toISOString().slice(0, 10).replace(/-/g, "")
      : date;
  }

  const { data, loading, error } = useFetchData(symbolListName, formattedDate);

  const [[selectedTicker, selectedName], setSelectedTickerAndName] = useState<
    [string, string]
  >(["", ""]);

  const columns = useMemo(() => columnsConfig, [columnsConfig]);

  const tableInstance = useTable({ columns, data }, useSortBy);

  const { getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } =
    tableInstance;

  const handleRowClick = (ticker: string, name: string) => {
    setSelectedTickerAndName([ticker, name]);
  };

  const chartData = useFetchCandlestickChartData(`symbol/${selectedTicker}`);

  const renderCandlestickChart = () => {
    if (chartData.length > 0) {
      return <CandlestickChart data={chartData[0].data} showRsi={true} />;
    } else {
      return <LoadingAnimation />;
    }
  };

  if (loading) {
    return (
      <div className="page">
        <div>
          <Typography
            variant="h5"
            component="h1"
            gutterBottom
            sx={{ borderBottom: "2px solid black", display: "inline-block" }}
          >
            {symbolListName}
          </Typography>
        </div>

        <Box padding={3}>
          <CalendarComponent
            initialDate={formattedDate}
            onDateUpdate={setFormattedDate}
          />
        </Box>
        <LoadingAnimation />
      </div>
    );
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div className="page">
      <div>
        <Typography
          variant="h5"
          component="h1"
          gutterBottom
          sx={{ borderBottom: "2px solid black", display: "inline-block" }}
        >
          {symbolListName}
        </Typography>
        <Box padding={3}>
          <CalendarComponent
            initialDate={formattedDate}
            onDateUpdate={setFormattedDate}
          />
        </Box>
        {selectedTicker && (
          <div className="chart-container">
            <Typography variant="h6" component="h6" gutterBottom>
              {`${selectedTicker} - ${selectedName}`}
            </Typography>
            {renderCandlestickChart()}
          </div>
        )}
        <TableContainer
          component={Paper}
          style={{ maxHeight: "75vh", overflow: "auto" }}
          className="table-container"
        >
          <Table {...getTableProps()} stickyHeader>
            <TableHead>
              {headerGroups.map((headerGroup: any) => (
                <TableRow {...headerGroup.getHeaderGroupProps()}>
                  {headerGroup.headers.map((column: any) => (
                    <StickyTableCell
                      {...column.getHeaderProps(column.getSortByToggleProps())}
                      sortDirection={
                        column.isSorted
                          ? column.isSortedDesc
                            ? "desc"
                            : "asc"
                          : false
                      }
                    >
                      {column.render("Header")}
                      <TableSortLabel
                        active={column.isSorted}
                        direction={
                          column.isSorted
                            ? column.isSortedDesc
                              ? "desc"
                              : "asc"
                            : "asc"
                        }
                        {...column.getSortByToggleProps()}
                      />
                    </StickyTableCell>
                  ))}
                </TableRow>
              ))}
            </TableHead>
            <TableBody {...getTableBodyProps()}>
              {rows.map((row: any) => {
                prepareRow(row);
                return (
                  <HoverTableRow
                    {...row.getRowProps()}
                    onClick={() =>
                      handleRowClick(row.values.ticker, row.values.name)
                    }
                    selected={row.values.ticker === selectedTicker}
                  >
                    {row.cells.map((cell: any) => (
                      <TableCell {...cell.getCellProps()}>
                        {cell.render("Cell")}
                      </TableCell>
                    ))}
                  </HoverTableRow>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>
      </div>
    </div>
  );
};

export default RSIScreener;
