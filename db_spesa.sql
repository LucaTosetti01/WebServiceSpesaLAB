-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Mag 21, 2020 alle 22:51
-- Versione del server: 10.4.11-MariaDB
-- Versione PHP: 7.4.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_spesa`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `lista`
--

CREATE TABLE `liste` (
  `idLista` int(11) NOT NULL,
  `rifRichiesta` int(11) NOT NULL,
  `rifProdotto` int(11) NOT NULL,
  `quantita` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `prodotto`
--

CREATE TABLE `prodotti` (
  `idProdotto` int(11) NOT NULL,
  `genere` varchar(30) NOT NULL,
  `etichetta` longtext NOT NULL,
  `costo` double(6,2) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `marca` varchar(50) NOT NULL,
  `descrizione` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dump dei dati per la tabella `prodotto`
--

INSERT INTO `prodotti` (`idProdotto`, `genere`, `etichetta`, `costo`, `nome`, `marca`, `descrizione`) VALUES
(1, 'qwe', 'qwe', 88.00, 'qwe', 'qwe', 'qwe'),
(2, 'werwer', 'werwer', 99.00, 'wer', 'wer', 'wer');

-- --------------------------------------------------------

--
-- Struttura della tabella `richiesta`
--

CREATE TABLE `richieste` (
  `idRichiesta` int(11) NOT NULL,
  `rifUtente` int(11) NOT NULL,
  `oraInizioConsegna` time NOT NULL,
  `oraFineConsegna` time NOT NULL,
  `durataRichiesta` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dump dei dati per la tabella `richiesta`
--

INSERT INTO `richieste` (`idRichiesta`, `rifUtente`, `oraInizioConsegna`, `oraFineConsegna`, `durataRichiesta`) VALUES
(1, 1, '20:41:18', '20:41:18', '20:41:18'),
(2, 2, '20:41:18', '20:41:18', '20:41:18'),
(3, 1, '12:00:00', '13:00:00', '01:00:00'),
(4, 2, '12:00:00', '13:00:00', '01:00:00'),
(5, 1, '13:00:00', '14:00:00', '01:00:00'),
(6, 3, '13:00:00', '14:00:00', '01:00:00');

-- --------------------------------------------------------

--
-- Struttura della tabella `risposta`
--

CREATE TABLE `risposte` (
  `idRisposta` int(11) NOT NULL,
  `rifUtente` int(11) NOT NULL,
  `rifRichiesta` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `utente`
--

CREATE TABLE `utenti` (
  `idUtente` int(11) NOT NULL,
  `username` varchar(30) NOT NULL,
  `nome` varchar(30) NOT NULL,
  `cognome` varchar(30) NOT NULL,
  `password` varchar(32) NOT NULL,
  `codiceFiscale` varchar(16) NOT NULL,
  `regione` varchar(30) NOT NULL,
  `via` varchar(30) NOT NULL,
  `nCivico` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dump dei dati per la tabella `utente`
--

INSERT INTO `utenti` (`idUtente`, `username`, `nome`, `cognome`, `password`, `codiceFiscale`, `regione`, `via`, `nCivico`) VALUES
(1, 'asdasd', 'asdasd', 'asdasd', 'asdasd', 'asdasd', 'asdasd', 'asdasd', 'asd'),
(2, 'werwer', 'werwer', 'werwer', 'werwer', 'werwer', 'werwer', 'werwer', 'wer'),
(3, 'ert', 'ert', 'ert', 'ert', 'ert', 'ert', 'ert', 'ert'),
(4, 'rtyrty', 'asdasd', 'asdasd', 'asd', 'asdasd', 'asdasd', 'asdasd', 'asd'),
(5, 'wer', 'wer', 'wer', 'wer', 'wer', 'wer', 'wer', 'wer'),
(6, 'yui', 'ert', 'ert', 'ert', 'ert', 'ert', 'ert', 'ert'),
(8, 'aui', 'ert', 'ert', 'ert', 'ert', 'ert', 'ert', 'ert');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `lista`
--
ALTER TABLE `liste`
  ADD PRIMARY KEY (`idLista`),
  ADD KEY `associazione4` (`rifRichiesta`),
  ADD KEY `associazione5` (`rifProdotto`);

--
-- Indici per le tabelle `prodotto`
--
ALTER TABLE `prodotti`
  ADD PRIMARY KEY (`idProdotto`);

--
-- Indici per le tabelle `richiesta`
--
ALTER TABLE `richieste`
  ADD PRIMARY KEY (`idRichiesta`),
  ADD KEY `associazione1` (`rifUtente`);

--
-- Indici per le tabelle `risposta`
--
ALTER TABLE `risposte`
  ADD PRIMARY KEY (`idRisposta`),
  ADD KEY `associazione2` (`rifUtente`),
  ADD KEY `associazione3` (`rifRichiesta`);

--
-- Indici per le tabelle `utente`
--
ALTER TABLE `utenti`
  ADD PRIMARY KEY (`idUtente`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `lista`
--
ALTER TABLE `liste`
  MODIFY `idLista` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `prodotto`
--
ALTER TABLE `prodotti`
  MODIFY `idProdotto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `richiesta`
--
ALTER TABLE `richieste`
  MODIFY `idRichiesta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT per la tabella `risposta`
--
ALTER TABLE `risposte`
  MODIFY `idRisposta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `utente`
--
ALTER TABLE `utenti`
  MODIFY `idUtente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `lista`
--
ALTER TABLE `liste`
  ADD CONSTRAINT `associazione4` FOREIGN KEY (`rifRichiesta`) REFERENCES `richieste` (`idRichiesta`),
  ADD CONSTRAINT `associazione5` FOREIGN KEY (`rifProdotto`) REFERENCES `prodotti` (`idProdotto`);

--
-- Limiti per la tabella `richiesta`
--
ALTER TABLE `richieste`
  ADD CONSTRAINT `associazione1` FOREIGN KEY (`rifUtente`) REFERENCES `utenti` (`idUtente`);

--
-- Limiti per la tabella `risposta`
--
ALTER TABLE `risposte`
  ADD CONSTRAINT `associazione2` FOREIGN KEY (`rifUtente`) REFERENCES `utenti` (`idUtente`),
  ADD CONSTRAINT `associazione3` FOREIGN KEY (`rifRichiesta`) REFERENCES `richieste` (`idRichiesta`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
