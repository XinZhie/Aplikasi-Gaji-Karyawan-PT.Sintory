-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 19, 2025 at 08:48 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbaplikasigajikaryawan`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbgaji`
--

CREATE TABLE `tbgaji` (
  `id` int(11) NOT NULL,
  `ktp` varchar(20) DEFAULT NULL,
  `kodepekerjaan` varchar(10) DEFAULT NULL,
  `gajibersih` decimal(15,2) DEFAULT NULL,
  `gajikotor` decimal(15,2) DEFAULT NULL,
  `tunjangan` decimal(15,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbgaji`
--

INSERT INTO `tbgaji` (`id`, `ktp`, `kodepekerjaan`, `gajibersih`, `gajikotor`, `tunjangan`) VALUES
(2, '221011450474', 'P002', 3000000.00, 3200000.00, 500000.00),
(9, '12345', 'P002', 10000.00, 1000.00, 1000.00),
(12, '123', 'P001', 12345.00, 12345.00, 12345.00);

-- --------------------------------------------------------

--
-- Table structure for table `tbkaryawan`
--

CREATE TABLE `tbkaryawan` (
  `ktp` varchar(20) NOT NULL,
  `nama` varchar(100) DEFAULT NULL,
  `ruang` int(11) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbkaryawan`
--

INSERT INTO `tbkaryawan` (`ktp`, `nama`, `ruang`, `password`) VALUES
('123', 'asep', 1, '202cb962ac59075b964b07152d234b70'),
('12345', 'udin', 2, '827ccb0eea8a706c4c34a16891f84e7b'),
('123456789', 'Wira', 1, '25f9e794323b453885f5181f1b624d0b'),
('221011450474', 'Randi Abi Fahrezi', 1, '827ccb0eea8a706c4c34a16891f84e7b');

-- --------------------------------------------------------

--
-- Table structure for table `tbpekerjaan`
--

CREATE TABLE `tbpekerjaan` (
  `kodepekerjaan` varchar(10) NOT NULL,
  `namapekerjaan` varchar(100) DEFAULT NULL,
  `jumlahtugas` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbpekerjaan`
--

INSERT INTO `tbpekerjaan` (`kodepekerjaan`, `namapekerjaan`, `jumlahtugas`) VALUES
('P001', 'Manager', 3),
('P002', 'Staff', 6),
('P003', 'IT Support', 8);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbgaji`
--
ALTER TABLE `tbgaji`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ktp` (`ktp`),
  ADD KEY `kodepekerjaan` (`kodepekerjaan`);

--
-- Indexes for table `tbkaryawan`
--
ALTER TABLE `tbkaryawan`
  ADD PRIMARY KEY (`ktp`);

--
-- Indexes for table `tbpekerjaan`
--
ALTER TABLE `tbpekerjaan`
  ADD PRIMARY KEY (`kodepekerjaan`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbgaji`
--
ALTER TABLE `tbgaji`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tbgaji`
--
ALTER TABLE `tbgaji`
  ADD CONSTRAINT `tbgaji_ibfk_1` FOREIGN KEY (`ktp`) REFERENCES `tbkaryawan` (`ktp`),
  ADD CONSTRAINT `tbgaji_ibfk_2` FOREIGN KEY (`kodepekerjaan`) REFERENCES `tbpekerjaan` (`kodepekerjaan`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
