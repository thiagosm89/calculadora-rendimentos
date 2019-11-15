package com.calculadora;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;

import static java.math.RoundingMode.HALF_UP;

public class Main {

    public static void main(String[] args) {

        boolean continuar = true;

        while (continuar) {
            int arredondar = 15;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            Scanner s = new Scanner(System.in);

            System.out.print("Digite o valor do investimento: ");
            BigDecimal entrada = s.nextBigDecimal();

            System.out.print("Valor líquido retirada: ");
            BigDecimal saida = s.nextBigDecimal();
            BigDecimal rendimento = saida.subtract(entrada);

            System.out.println("Data de compra: " + LocalDate.now().format(formatter));
            LocalDate dataCompra = LocalDate.parse(s.next(), formatter);
            //LocalDate dataCompra = LocalDate.now();

            System.out.print("Data de venda: ");
            LocalDate dataVenda = LocalDate.parse(s.next(), formatter);

            System.out.print("Inflação Acumulada IPCA: ");

            Period between = Period.between(dataCompra, dataVenda);
            BigDecimal percentualIpcaAno = s.nextBigDecimal();
            BigDecimal percentualIpcaMediaMes = percentualIpcaAno.divide(new BigDecimal(LocalDate.now().getMonthValue()), 4, HALF_UP);

            Optional<Integer> meses = Optional.of(between.getYears())
                    .map(totalAnos -> totalAnos * 12)
                    .map(totalMeses -> totalMeses + between.getMonths());

            Integer totalMeses = meses.get();
            System.out.println("\n====>\n");
            System.out.println("Tempo retorno investimento: " + between.getYears() + " ano, " + between.getMonths() + " meses, " + between.getYears() + " dias");

            BigDecimal percentualRendimentoNominal = rendimento.divide(entrada, arredondar, HALF_UP).multiply(new BigDecimal(100));
            BigDecimal percentualMensalNominal = percentualRendimentoNominal.divide(new BigDecimal(totalMeses), arredondar, HALF_UP);
            Optional<BigDecimal> percentualDiarioNominal = Optional.of(between.getDays())
                    .map(days -> {
                        if (days == 0) {
                            return new BigDecimal(days);
                        } else {
                            BigDecimal percentualD = percentualMensalNominal.divide(new BigDecimal(30), arredondar, HALF_UP);
                            return percentualD.multiply(new BigDecimal(days));
                        }
                    });

            BigDecimal percentualRendimentoNominalMes = percentualMensalNominal.add(percentualDiarioNominal.get()).setScale(arredondar, HALF_UP);
            BigDecimal percentualRendimentoNominalAno = percentualRendimentoNominalMes.multiply(new BigDecimal(12)).setScale(arredondar, HALF_UP);

            BigDecimal ra = percentualRendimentoNominalAno.divide(new BigDecimal(100), arredondar, HALF_UP).add(new BigDecimal(1));
            BigDecimal pia = percentualIpcaAno.divide(new BigDecimal(100), arredondar, HALF_UP).add(new BigDecimal(1));
            BigDecimal ipcaRealAno = ra.divide(pia, arredondar, HALF_UP).subtract(new BigDecimal(1)).multiply(new BigDecimal(100)).setScale(arredondar, HALF_UP);


            System.out.println("Percentual de rendimento total: " + percentualRendimentoNominal.setScale(2, HALF_UP) + "%");
            System.out.println("Percentual ao ano: " + percentualRendimentoNominalAno.setScale(2, HALF_UP) + "%");
            System.out.println("Percentual ao ano (considerando inflação): " + ipcaRealAno.setScale(2, HALF_UP) + "%");
            System.out.println("Percentual ao mês: " + percentualRendimentoNominalMes.setScale(2, HALF_UP) + "%");

            System.out.println("\n");
            System.out.print("Novo cálculo? (S ou N): ");
            String novoCalculo = s.next().trim().toLowerCase();
            if( !novoCalculo.equals("s") ) {
                continuar = false;
            }
            System.out.println("\n\n\n");
        }
    }
}
