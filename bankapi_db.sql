-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Czas generowania: 09 Sty 2023, 09:35
-- Wersja serwera: 10.4.21-MariaDB
-- Wersja PHP: 8.0.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `bankapi_db`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `accounts`
--

CREATE TABLE `accounts` (
  `account_num` varchar(26) NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  `surname` varchar(30) DEFAULT NULL,
  `balance` decimal(8,2) DEFAULT NULL,
  `password` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Zrzut danych tabeli `accounts`
--

INSERT INTO `accounts` (`account_num`, `name`, `surname`, `balance`, `password`) VALUES
('11112222', 'sklep', 'sklepJDBC', '20618.95', 'sklepik123'),
('123456', 'Maks', 'Wielgomasz', '338.59', 'lechpoznan');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `blik_nums`
--

CREATE TABLE `blik_nums` (
  `blik_num` varchar(6) NOT NULL,
  `demanded_money` decimal(8,2) DEFAULT NULL,
  `account_num` varchar(26) DEFAULT NULL,
  `target_account` varchar(26) DEFAULT NULL,
  `status` varchar(14) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Zrzut danych tabeli `blik_nums`
--

INSERT INTO `blik_nums` (`blik_num`, `demanded_money`, `account_num`, `target_account`, `status`) VALUES
('009933', '246.12', '123456', '11112222', 'to_confirm'),
('247596', '127.78', '123456', '11112222', 'to_confirm'),
('276244', '204.20', '123456', '11112222', 'expired'),
('511712', '294.70', '123456', '11112222', 'to_confirm'),
('523263', '265.49', '123456', '11112222', 'used'),
('532172', '337.68', '123456', '11112222', 'to_confirm'),
('702019', '332.23', '123456', '11112222', 'used');

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`account_num`);

--
-- Indeksy dla tabeli `blik_nums`
--
ALTER TABLE `blik_nums`
  ADD PRIMARY KEY (`blik_num`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
